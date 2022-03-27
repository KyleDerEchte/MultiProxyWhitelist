package de.kyleonaut.multiproxywhitelist.repository;

import de.kyleonaut.multiproxywhitelist.model.PlayerProfile;
import de.kyleonaut.multiproxywhitelist.request.MojangRequests;
import lombok.RequiredArgsConstructor;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

/**
 * @author kyleonaut
 */
@RequiredArgsConstructor
public class MojangRepository {
    private final MojangRequests mojangRequests;

    public PlayerProfile getPlayerProfileByName(String name) {
        final Call<PlayerProfile> call = mojangRequests.getPlayerProfileByUUID(name);
        try {
            final Response<PlayerProfile> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
