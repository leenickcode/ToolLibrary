package com.example.arcmodekt.http

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

abstract class NetworkBoundResource <ResultType, RequestType>{
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        @Suppress("LeakingThis")
        val dbSource = loadFromDb()
//        if (dbSource.value==null){
//            fetchFromNetwork(dbSource)
//        }else{
            result.addSource(dbSource) { data ->
                result.removeSource(dbSource)
                if (shouldFetch(data)) {
                    fetchFromNetwork(dbSource)
                } else {
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.success(newData))
                    }
                }
            }
//        }

    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    /**
     * 从网络获取数据
     */
    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newData ->
            setValue(Resource.loading(newData))
        }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response) {
                is ApiSuccessResponse -> {
                    //更新成功，刷新本地数据
                    saveCallResult(processResponse(response))
                    result.addSource(loadFromDb()) { newData ->
                        setValue(Resource.success(newData))
                    }
                }
                is ApiEmptyResponse -> {
//                    appExecutors.mainThread().execute {
//                        // reload from disk whatever we had
//                        result.addSource(loadFromDb()) { newData ->
//                            setValue(Resource.success(newData))
//                        }
//                    }
                }
                is ApiErrorResponse -> {
                    onFetchFailed()
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.error(response.errorMessage, newData))
                    }
                }
            }

        }
    }

    /**
     * 获取数据失败
     */
    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    /**
     * 将remote获取的数据保存本地
     */
    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    /**
     * 是否需要更新remote数据
     */
    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    /**
     * load本地数据
     */
    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    /**
     * 调用网络请求
     */
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}