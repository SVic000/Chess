package client;

import client.error.ClientError;
import com.google.gson.Gson;

public class Serializer {
        public ClientError decrypt(Throwable e) {
            return parse(e.getMessage());
        }
        public ClientError decrypt(Exception e) {
            return parse(e.getMessage());
        }

        private ClientError parse(String message) {
            if (message == null) {
                return new ClientError("Unknown error occurred.", 0);
            }
            try {
                return new Gson().fromJson(message, ClientError.class);
            } catch (Exception ex) {
                return new ClientError(message, 0);
            }
        }
}
