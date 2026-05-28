namespace Carpathians.MAUI.Models;

public class RoutePointModel
{
    public string Title { get; set; }
    public string Altitude { get; set; }
    public string Description { get; set; }
    public string Image { get; set; }
    public int Order { get; set; }
    public bool HasImage => !string.IsNullOrEmpty(Image);
}