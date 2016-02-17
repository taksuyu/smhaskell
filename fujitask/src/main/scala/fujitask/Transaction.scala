package fujitask

/** Represents a transaction type where two permission levels exist: "Read" and "Read/Write".
  * "Read/Write" extends "Read", expressing that "Read" tasks can be also processed within a "Read/Write" transaction.
  */
trait Transaction

trait ReadTransaction extends Transaction

trait ReadWriteTransaction extends ReadTransaction

// ReadTransaction <: ReadWriteTransaction
// ReadWriteTransaction :> ReadTransaction
