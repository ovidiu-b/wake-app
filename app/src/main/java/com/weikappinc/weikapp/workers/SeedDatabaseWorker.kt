package com.weikappinc.weikapp.workers

/*
class SeedDatabaseWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            val database = AppDatabase.getAppDatabase(applicationContext)
            val currentUserDataDao = database?.currentUserData()

            val currentUser = CurrentUserData("b.ovidiu.2312@gmail.com", "Ovidiu")

            currentUserDataDao?.insert(currentUser)

            Result.success()
        }catch (ex: Exception) {
            Log.e("SeedDatabaseWorker", "Error seeding database", ex)
            Result.failure()
        }
    }
}*/
