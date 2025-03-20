package videos.religious.platform;

public interface IOnBackPressed {
    boolean AllowBack();
    void onBackPressed();
    void check(String no);
    void setCurrentFragment(int i);
    void setRefresh(int i);
    void setNewFragment(int fragment);
}