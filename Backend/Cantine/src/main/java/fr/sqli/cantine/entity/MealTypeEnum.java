package fr.sqli.cantine.entity;

public enum MealTypeEnum {
    ENTREE,
    PLAT,
    DESSERT,
    BOISSON,
    ACCOMPAGNEMENT,
    AUTRE;




    public static boolean contains(String mealType) {
        for (MealTypeEnum mealTypeEnum : MealTypeEnum.values()) {
            if (mealTypeEnum.name().equals(mealType)) {
                return true;
            }
        }
        return false;
    }

    public  static  MealTypeEnum getMealTypeEnum(String mealType){
        for (MealTypeEnum mealTypeEnum : MealTypeEnum.values()) {
            if (mealTypeEnum.name().equals(mealType)) {
                return mealTypeEnum;
            }
        }
        return null; //  this case will never happen because we already checked the mealType
    }

}
