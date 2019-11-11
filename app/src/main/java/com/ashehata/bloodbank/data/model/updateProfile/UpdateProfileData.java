
package com.ashehata.bloodbank.data.model.updateProfile;

import com.ashehata.bloodbank.data.model.createAccount.Client;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateProfileData {

    @SerializedName("client")
    @Expose
    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

}
