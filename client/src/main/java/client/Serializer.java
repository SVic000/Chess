package client;

import com.google.gson.Gson;

public class Serializer {
    public ClientError decrypt(Throwable e) {
        return new Gson().fromJson(e.getMessage(), ClientError.class);
    }
    public ClientError decrypt(Exception e) {
        return new Gson().fromJson(e.getMessage(), ClientError.class);
    }
}
