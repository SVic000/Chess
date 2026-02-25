package server.Handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.UserData;

public class UserHandler(Context ctx){
    /*
        Pet pet = new Gson().fromJson(ctx.body(), Pet.class);
        pet = service.addPet(pet);
        webSocketHandler.makeNoise(pet.name(), pet.sound());
        ctx.result(new Gson().toJson(pet));
     */
    UserData userData = new Gson().fromJson(ctx.body(), UserData.class);

    }
}
