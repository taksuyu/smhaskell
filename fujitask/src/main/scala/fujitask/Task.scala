package fujitask

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** This `Task` Monad aims to provide two functionality:
  *   1. abstracts out a transaction with database like the Reader Monad
  *   2. prevent permission errors of transactions at compile time
  *
  * [Definition Of "Transaction" In This Library]
  * "Transaction" is a set of tasks to a database.
  * "Transaction object" is an object that represents a transaction,
  * which some database libraries name as "session object".
  *
  * [Monadic Approach To Transaction]
  * A `Task` object represents a transaction.
  * A Task has a computation to gain a value (whose type is `A`.)
  * The `foldMap` method binds those computations to be executed in an order,
  * and the `run` method executes them all as a transaction, just like Reader Monad.
  *
  * This implements PofEAA's "Unit of Work":
  * http://martinfowler.com/eaaCatalog/unitOfWork.html
  *
  * [Two Types Of Transaction Permission And Runtime Error]
  * There are two types of transaction: "Read" and "Read/Write".
  * In common master/slave structured database,
  * "Read/Write" transactions must ask the master to apply a modification.
  * "Read/Write" transactions asking a slave instead may cause a runtime error,
  * because only the master has a permission to modify the database.
  *
  * [Determining Permission Level At Compile Time]
  * Each Task is tagged with a transaction type (namely `Resource`)
  * which represents its permission level (cf. whether its query must ask the master or a slave.)
  *
  * `foldMap` restricts a given monadic function's transaction type to be
  * equivalent or contravariant to its own Task's transaction type.
  * So, if the "Read/Write" transaction type is set to be a subtype of the "Read",
  * `foldMap` reproduces a "Read/Write" task when a "Read" task is bounded to a "Read/Write" task.
  * Thus the above problem, accidentally asking a slave to modify the database, is prevented at compile time.
  *
  * `Transaction.scala` defines a "Read/Write" transaction type inheriting "Read",
  * and it is expected to be useful in case of a master/slave database.
  *
  * @tparam Resource determines this Task's permission level.
  * @tparam A determines the type of value that this Task will receive when executed.
 */
trait Task[-Resource, +A] { self =>
  /** Executes this task's process.
    *
    * @param resource determines this Task's permission level.
    * @param ec ExecutionContext
    * @return value that this Task aims to receive from the database.
    */
  def execute(resource: Resource)(implicit ec: ExecutionContext): Future[A]

  /** Binds this Task (Monadic bind.)
    * See the above explanation.
    *
    * @param f Monadic function toa new Task.
    * @tparam ExtendedResource determines the reproduced Task's transaction type.
    */
  def flatMap[ExtendedResource <: Resource, B](f: A => Task[ExtendedResource, B]): Task[ExtendedResource, B] =
    new Task[ExtendedResource, B] {
      def execute(resource: ExtendedResource)(implicit ec: ExecutionContext): Future[B] =
        self.execute(resource).map(f).flatMap(_.execute(resource))
    }

  /** Maps a given function to the result of this task.
    */
  def map[B](f: A => B): Task[Resource, B] = flatMap(a => Task(f(a)))

  /** Executes this Task using a `TaskRunner` which corresponds to this Task's transaction.
    * The TaskRunner may be chosen implicitly.
    *
    * @param runner TaskRunner to execute this Task.
    * @tparam ExtendedResource determines the type of transaction.
    */
  def run[ExtendedResource <: Resource]()(implicit runner: TaskRunner[ExtendedResource]): Future[A] = {
    runner.run(this)
  }
}

object Task {
  /** Constructs a Task with a given value.
    *
    * @param a value to be put into this Task. Lazily evaluated.
    * @tparam Resource represents this Task's permission level.
    */
  def apply[Resource, A](a: => A): Task[Resource, A] =
    new Task[Resource, A] {
      def execute(resource: Resource)(implicit executor: ExecutionContext): Future[A] =
        Future(a)
    }
}

/** Executes a Task of the specified transaction type.
  * This instance has to be defined for each transaction type.
  *
  * @tparam Resource represents the transaction type that this instance supports.
  */
trait TaskRunner[Resource] {
  /** Runs a given Task whose transaction type matches with this runner's.
    */
  def run[A](task: Task[Resource, A]): Future[A]
}
