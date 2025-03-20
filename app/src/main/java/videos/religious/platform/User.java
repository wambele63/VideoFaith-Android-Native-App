package videos.religious.platform;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class User {
    @Id
    public long id;
    private String FullName,ImageUrl,ChannelName,Email,Phone,Password,Gender,Religion,Age;

    public void setEmail(String email){
        this.Email = email;
    }
    public String getEmail(){
        return this.Email;
    }
    public void setPhone(String phone){
        this.Phone = phone;
    }
    public String getPhone(){
        return this.Phone;
    }
    public void setPassword(String password){
        this.Password = password;
    }
    public String getPassword(){
        return this.Password;
    }
    public void setFullName(String fullName){
        this.FullName = fullName;
    }
    public String getFullName(){
        return this.FullName;
    }
    public void setGender(String gender){
        this.Gender = gender;
    }
    public String getGender(){
        return this.Gender;
    }
    public void setAge(String age){
        this.Age = age;
    }
    public String getAge(){
        return this.Age;
    }
    public void setReligion(String religion){
        this.Religion = religion;
    }
    public String getReligion(){
        return this.Religion;
    }
    public void setChannelName(String channelName){
        this.ChannelName = channelName;
    }
    public String getChannelName(){
        return this.ChannelName;
    }
    public void setImageUrl(String imageUrl){
        this.ImageUrl = imageUrl;
    }
    public void updateFullName(String newName){
        this.FullName = newName;
    }
    public String getImageUrl(){
        return this.ImageUrl;
    }
    public long getId(){
        return this.id;
    }
}