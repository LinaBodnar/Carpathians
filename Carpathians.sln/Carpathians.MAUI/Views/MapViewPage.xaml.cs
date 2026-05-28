namespace Carpathians.MAUI.Views;

[QueryProperty(nameof(Url), "url")]
public partial class MapViewPage : ContentPage
{
    private string _url;
    public string Url
    {
        get => _url;
        set
        {
            _url = value;
            if (!string.IsNullOrEmpty(value))
                mapWebView.Source = value;
        }
    }

    public MapViewPage()
    {
        InitializeComponent();
    }
}