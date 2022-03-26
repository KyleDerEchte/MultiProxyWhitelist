package de.kyleonaut.multiproxywhitelist.request;

import de.kyleonaut.multiproxywhitelist.model.PlayerProfile;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MojangRequests {

    @GET("users/profiles/minecraft/{username}")
    Call<PlayerProfile> getPlayerProfileByUUID(@Path(value = "username") String name);
}