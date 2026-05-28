using System.Collections.ObjectModel;
using System.Threading.Tasks;
using System.Windows.Input;
using System.Linq;
using Carpathians.MAUI.Models;
using Carpathians.MAUI.Services;

namespace Carpathians.MAUI.ViewModels;

public class GalleryViewModel : BaseViewModel
{
    private readonly IDataService _dataService;
    private string _activeFilter = "Всі";

    public ObservableCollection<GalleryPhotoModel> AllPhotos { get; } = new();
    public ObservableCollection<GalleryPhotoModel> FilteredPhotos { get; } = new();

    public string ActiveFilter
    {
        get => _activeFilter;
        set { _activeFilter = value; OnPropertyChanged(); }
    }

    public ICommand FilterCommand { get; }
    public ICommand LoadPhotosCommand { get; }

    public GalleryViewModel(IDataService dataService)
    {
        _dataService = dataService;
        LoadPhotosCommand = new Command(async () => await LoadPhotosAsync());
        FilterCommand = new Command<string>(FilterPhotos);
    }

    private async Task LoadPhotosAsync()
    {
        if (IsBusy) return;
        IsBusy = true;
        try
        {
            var photos = await _dataService.GetAllPhotosAsync();
            AllPhotos.Clear();
            foreach (var p in photos) AllPhotos.Add(p);
            FilterPhotos("Всі");
        }
        catch
        {
            if (App.Current?.MainPage != null)
                await App.Current.MainPage.DisplayAlert("Помилка", "Не вдалося завантажити фото", "OK");
        }
        finally
        {
            IsBusy = false;
        }
    }

    private void FilterPhotos(string filter)
    {
        ActiveFilter = filter;
        FilteredPhotos.Clear();
        var q = filter == "Всі" ? AllPhotos : AllPhotos.Where(p => p.RouteKey == filter);
        foreach (var p in q) FilteredPhotos.Add(p);
    }
}