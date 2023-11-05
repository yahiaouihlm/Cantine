package fr.sqli.cantine.dto.out.person;

import fr.sqli.cantine.entity.AdminEntity;

public class AdminDtout extends AbstractPersonDtout{

   private  String function;

    public AdminDtout(AdminEntity adminEntity , String  imageUrl){
            super.setUuid(adminEntity.getUuid());
            super.setFirstname(adminEntity.getFirstname());
            super.setLastname(adminEntity.getLastname());
            super.setEmail(adminEntity.getEmail());
            super.setBirthdate(adminEntity.getBirthdate());
            super.setAddress(adminEntity.getAddress());
            super.setTown(adminEntity.getTown());
            super.setPhone(adminEntity.getPhone());
            super.setImage(imageUrl + adminEntity.getImage().getImagename());
            this.setFunction(adminEntity.getFunction().getName());
    }




    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

}
