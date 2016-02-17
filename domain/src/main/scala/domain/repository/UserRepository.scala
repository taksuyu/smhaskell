package domain.repository

import fujitask.Task
import fujitask.ReadTransaction
import fujitask.ReadWriteTransaction
import domain.entity.User

/** Defines each transaction process that handles User data with a database.
  */
trait UserRepository {
  /** Creating a user account in the database requires his/her name,
    * and the permission level must be higher than "Read/Write".
    */
  def create(name: String): Task[ReadWriteTransaction, User]

  def read(id: Long): Task[ReadTransaction, Option[User]]

  def readAll: Task[ReadTransaction, List[User]]

  def update(user: User): Task[ReadWriteTransaction, Unit]

  def delete(id: Long): Task[ReadWriteTransaction, Unit]
}
