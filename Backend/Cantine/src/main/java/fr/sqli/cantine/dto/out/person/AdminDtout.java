package fr.sqli.cantine.dto.out.person;


import fr.sqli.cantine.entity.UserEntity;

public class AdminDtout extends AbstractPersonDtout{

   private  String function;

    public AdminDtout(UserEntity adminEntity , String  imageUrl){
            super.setId(adminEntity.getId());
            super.setFirstname(adminEntity.getFirstname());
            super.setLastname(adminEntity.getLastname());
            super.setEmail(adminEntity.getEmail());
            super.setBirthdate(adminEntity.getBirthdate());
            super.setAddress(adminEntity.getAddress());
            super.setTown(adminEntity.getTown());
            super.setPhone(adminEntity.getPhone());
            super.setImage(imageUrl + adminEntity.getImage().getName());
            this.setFunction(adminEntity.getFunction().getName());
    }




    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

}
