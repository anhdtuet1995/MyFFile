package work.uet.anhdt.ftpstorageofficial.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import work.uet.anhdt.ftpstorageofficial.models.GetFiles;
import work.uet.anhdt.ftpstorageofficial.util.Constant;

/**
 * Created by anansaj on 11/18/2017.
 */

public interface GetFilesAPI {

    @GET(Constant.GET_FILES)
    Call<GetFiles> getAllFiles();

}
