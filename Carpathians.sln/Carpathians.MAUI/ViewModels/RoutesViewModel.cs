using System.Collections.ObjectModel;
using System.Threading.Tasks;
using System.Windows.Input;
using System.Linq;
using Carpathians.MAUI.Models;
using Carpathians.MAUI.Services;

namespace Carpathians.MAUI.ViewModels;

public class RoutesViewModel : BaseViewModel
{
    private readonly IDataService _dataService;
    private RouteModel? _selectedRoute;
    private string _activeRouteKey = "ALL";
    private List<RouteModel> _allRoutes = new();

    public ObservableCollection<RouteModel> Routes { get; } = new();

    public RouteModel? SelectedRoute
    {
        get => _selectedRoute;
        set { _selectedRoute = value; OnPropertyChanged(); }
    }

    public string ActiveRouteKey
    {
        get => _activeRouteKey;
        set { _activeRouteKey = value; OnPropertyChanged(); }
    }

    public ICommand LoadRoutesCommand { get; }
    public ICommand GoToDetailCommand { get; }
    public ICommand SelectRouteTabCommand { get; }

    public RoutesViewModel(IDataService dataService)
    {
        _dataService = dataService;
        Title = "Adventure";
        LoadRoutesCommand = new Command(async () => await LoadRoutesAsync());
        GoToDetailCommand = new Command<RouteModel>(async (route) => await GoToDetailAsync(route));
        SelectRouteTabCommand = new Command<string>(SelectRouteTab);
    }

    private async Task LoadRoutesAsync()
    {
        if (IsBusy) return;
        IsBusy = true;
        try
        {
            var routes = await _dataService.GetAllRoutesAsync();
            _allRoutes = routes.ToList();
            FilterRoutes();
        }
        catch
        {
            if (App.Current?.MainPage != null)
                await App.Current.MainPage.DisplayAlert("Помилка", "Не вдалося завантажити дані", "OK");
        }
        finally
        {
            IsBusy = false;
        }
    }

    private async Task GoToDetailAsync(RouteModel? route)
    {
        if (route == null) return;
        await Shell.Current.GoToAsync($"routedetail?key={route.Key}");
    }

    private void SelectRouteTab(string filter)
    {
        ActiveRouteKey = filter;
        FilterRoutes();
    }

    private void FilterRoutes()
    {
        Routes.Clear();
        foreach (var route in _allRoutes)
        {
            if (ActiveRouteKey == "ALL" || route.Key == ActiveRouteKey)
                Routes.Add(route);
        }
    }
}