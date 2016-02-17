{-# LANGUAGE MultiParamTypeClasses #-}

class Transaction t where

data ReadWrite
data ReadOnly

instance Transaction ReadWrite
instance Transaction ReadOnly

data Task t a = Task a

type UserId = Int

findUser :: String -> Task ReadOnly UserId
findUser = undefined

deleteUser :: UserId -> Task ReadWrite ()
deleteUser = undefined

elevateTask :: Task ReadOnly a -> Task ReadWrite a
elevateTask = undefined

bindTask :: Transaction t => Task t a -> (a -> Task t b) -> Task t b
bindTask = undefined

sendToMaster :: Transaction t => Task t a -> IO ()
sendToMaster = undefined

sendToSlave :: Task ReadOnly a -> IO ()
sendToSlave = undefined

findUser "Jake"
deleteUser 42
bindTask (elevateTask $ findUser "Jake") deleteUser
bindTask (findUser "Jake") deleteUser

sendToSlave $ deleteUser 42
sendToSlave $ findUser "Jake"