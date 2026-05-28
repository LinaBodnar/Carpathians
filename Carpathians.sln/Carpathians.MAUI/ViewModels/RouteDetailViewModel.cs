using System.Collections.ObjectModel;
using System.Threading.Tasks;
using System.Windows.Input;
using Carpathians.MAUI.Models;
using Carpathians.MAUI.Services;

namespace Carpathians.MAUI.ViewModels;

[QueryProperty(nameof(RouteKey), "key")]
public class RouteDetailViewModel : BaseViewModel
{
    private readonly IDataService _dataService;
    private string _routeKey = string.Empty;
    private RouteModel? _currentRoute;

    public string RouteKey
    {
        get => _routeKey;
        set { _routeKey = value; OnPropertyChanged(); _ = LoadRouteAsync(); }
    }

    public RouteModel? CurrentRoute
    {
        get => _currentRoute;
        set { _currentRoute = value; OnPropertyChanged(); }
    }

    public ObservableCollection<RoutePointModel> Points { get; } = new();

    public ICommand OpenMapCommand { get; }
    public ICommand GoToBookingCommand { get; }

    public RouteDetailViewModel(IDataService dataService)
    {
        _dataService = dataService;
        OpenMapCommand = new Command(async () => await OpenMapAsync());
        GoToBookingCommand = new Command(async () => await GoToBookingAsync());
    }

    private async Task LoadRouteAsync()
    {
        if (string.IsNullOrEmpty(RouteKey)) return;
        IsBusy = true;
        try
        {
            CurrentRoute = await _dataService.GetRouteByKeyAsync(RouteKey);
            Points.Clear();
            if (CurrentRoute != null)
            {
                Title = CurrentRoute.Title;
                foreach (var pt in CurrentRoute.Points) Points.Add(pt);
            }
        }
        finally
        {
            IsBusy = false;
        }
    }

    private async Task OpenMapAsync()
    {
        if (CurrentRoute == null) return;
        await Shell.Current.GoToAsync($"mapview?url={System.Net.WebUtility.UrlEncode(CurrentRoute.InteractiveUrl)}");
    }

    private async Task GoToBookingAsync()
    {
        await Shell.Current.GoToAsync($"//booking?route={RouteKey}");
    }
}