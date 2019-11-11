package com.ashehata.bloodbank.data.model;

import com.ashehata.bloodbank.data.model.bloodType.BloodType;
import com.ashehata.bloodbank.data.model.bloodType.BloodTypeData;
import com.ashehata.bloodbank.data.model.categey.Categey;
import com.ashehata.bloodbank.data.model.categey.CategeyData;
import com.ashehata.bloodbank.data.model.governorates.GonernoratesData;
import com.ashehata.bloodbank.data.model.governorates.Governorates;
import com.ashehata.bloodbank.data.model.login.LoginData;

import java.util.List;

public class ListModel {
    private List<BloodTypeData> bloodType ;
    private List<GonernoratesData> governorates ;
    private List<CategeyData> categories ;

    public List<BloodTypeData> getBloodType() {
        return bloodType;
    }

    public List<GonernoratesData> getGovernorates() {
        return governorates;
    }

    public List<CategeyData> getCategories() {
        return categories;
    }

    public void setBloodType(List<BloodTypeData> bloodType) {
        this.bloodType = bloodType;
    }

    public void setGovernorates(List<GonernoratesData> governorates) {
        this.governorates = governorates;
    }

    public void setCategories(List<CategeyData> categories) {
        this.categories = categories;
    }
}
