package happyshop.models;

/**
 * Created by Babar on 30-Apr-16.
 */
public class Product
{
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isUnderSale() {
        return underSale;
    }

    public void setUnderSale(boolean underSale) {
        this.underSale = underSale;
    }

    private int price;

    private String name;
    private String category;
    private String imgUrl;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    private boolean underSale;
}
