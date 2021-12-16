package com.example.arcmodekt.http;



import com.aratek.retrofit2demo.http.HttpResult;
import com.aratek.retrofit2demo.http.Resource;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    private MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<Resource<ResultType>>();


    public NetworkBoundResource() {
        result.setValue(Resource.Companion.loading(null));
        LiveData<ResultType> dbSource = loadFromDb();
        if (dbSource==null){
            fetchFromNetwork(null);
        }else {

        }



    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (result.getValue() != newValue) {
            result.setValue(newValue);
        }
    }

    private void fetchFromNetwork(LiveData<ResultType> dbSource) {
        LiveData<HttpResult<ResultType>> apiResponse = createCall();
//        result.addSource(dbSource, newData -> {
//            setValue(Resource.Companion.loading(newData));
//        });
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);

            setValue(Resource.Companion.success(response.getData()));
//            result.addSource(loadFromDb(), new Observer<ResultType>() {
//                @Override
//                public void onChanged(ResultType resultType) {
//                    setValue(Resource.Companion.success(resultType));
//                }
//            });
        });

    }

    protected void onFetchFailed() {
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    protected RequestType processResponse(HttpResult<RequestType> response) {
        return response.getData();
    }

    @WorkerThread
    protected abstract void saveCallResult(RequestType item);

    /**
     *  是否从网络中提取
     * @param data
     * @return
     */
    protected abstract Boolean shouldFetch(ResultType data);

    /**
     * 本地加载
     * @return
     */
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    /**
     * 网络请求
     * @return
     */
    @MainThread
    protected abstract LiveData<HttpResult<ResultType>> createCall();
}
