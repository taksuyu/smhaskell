naive :: IO ()
naive = do
  userId <- readFromSlave $ findUser "bob"
  sendToMaster $ deleteUser userId

-- These are semantically different as the naive solution would be grabbing the
-- information from a different source and then applying that change to a
-- Master. deleteUser could be done on our replica? of the database information
-- while sendToMaster would then sync it.

better :: IO ()
better = sendToMaster
         -- Our Unit of Work would be done here and preferrably permission
         -- checked when you call elevate. For example, some user might not have
         -- permission to elevate their access depending on the access of the
         -- server.
         $ elevate (findUser "bob") deleteUser

-- taskError :: IO ()
-- taskError = readFromSlave $ elevate (findUser "bob") deleteUser
-- Couldn't match type ‘ReadWrite’ with ‘ReadOnly’
--  Expected type: Task ReadOnly ()
--    Actual type: Task ReadWrite ()
--  In the second argument of ‘($)’, namely
--    ‘elevate (findUser "bob") deleteUser’
--  In the expression:
--    readFromSlave $ elevate (findUser "bob") deleteUser (haskell-stack-ghc)

data Task t a
  = Task a

instance Functor (Task t) where
  fmap fn (Task a) = Task (fn a)

instance Applicative (Task t) where
  pure = Task
  Task fn <*> Task a = Task (fn a)

instance Monad (Task t) where
  Task a >>= fn = fn a

data ReadOnly
data ReadWrite

class Transaction t

instance Transaction ReadOnly
instance Transaction ReadWrite

findUser :: String -> Task ReadOnly Int
findUser = undefined

deleteUser :: Int -> Task ReadWrite ()
deleteUser = undefined

-- bindTask is implemented through Monad
-- bindTask :: Transaction t => Task t a -> (a -> Task t b) -> Task t b

-- elevate elevates a ReadOnly Task through bind to a ReadWrite Task
elevate :: Task ReadOnly a -> (a -> Task ReadWrite b) -> Task ReadWrite b
elevate = undefined

-- Masters can read and write data interchangably
sendToMaster :: Transaction t => Task t a -> IO ()
sendToMaster = undefined

-- Forces the Task to be ReadOnly, but that doesn't mean the database can't
-- write data.
readFromSlave :: Task ReadOnly a -> IO a
readFromSlave = undefined
