using System.Collections.ObjectModel;
namespace Carpathians.MAUI.Models;

public class RouteModel
{
    public string Key { get; set; }
    public string Title { get; set; }
    public string MapImage { get; set; }
    public string InteractiveUrl { get; set; }
    public string Difficulty { get; set; }
    public string Length { get; set; }
    public string Duration { get; set; }
    public Color DifficultyColor => Difficulty switch
    {
        "Легкий" => Color.FromArgb("#4CAF50"),
        "Середній" => Color.FromArgb("#FFC107"),
        "Важкий" => Color.FromArgb("#F44336"),
        _ => Colors.White
    };
    public ObservableCollection<RoutePointModel> Points { get; set; } = new();
    public ObservableCollection<GalleryPhotoModel> Photos { get; set; } = new();
}