using System.Collections.ObjectModel;
using System.Threading.Tasks;
using System.Windows.Input;
using Carpathians.MAUI.Models;
using Carpathians.MAUI.Services;

namespace Carpathians.MAUI.ViewModels;

[QueryProperty(nameof(PreselectedRoute), "route")]
public class BookingViewModel : BaseViewModel
{
    private readonly IDataService _dataService;
    private readonly SessionService _sessionService;

    private string _name = string.Empty;
    private string _phone = string.Empty;
    private string _email = string.Empty;
    private string _selectedRoute = string.Empty;
    private int _peopleCount = 1;
    private bool _isSuccess;
    private string _preselectedRoute = string.Empty;

    public string Name { get => _name; set { _name = value; OnPropertyChanged(); } }
    public string Phone { get => _phone; set { _phone = value; OnPropertyChanged(); } }
    public string Email { get => _email; set { _email = value; OnPropertyChanged(); } }
    public string SelectedRoute { get => _selectedRoute; set { _selectedRoute = value; OnPropertyChanged(); } }
    public int PeopleCount { get => _peopleCount; set { _peopleCount = value; OnPropertyChanged(); } }
    public bool IsSuccess { get => _isSuccess; set { _isSuccess = value; OnPropertyChanged(); } }

    public string PreselectedRoute
    {
        get => _preselectedRoute;
        set { _preselectedRoute = value; OnPropertyChanged(); SelectedRoute = value; }
    }

    public bool IsLoggedIn => _sessionService.IsLoggedIn;
    public string? UserName => _sessionService.CurrentUser?.Name;
    public string? UserEmail => _sessionService.CurrentUser?.Email;

    public ObservableCollection<BookingModel> MyBookings { get; } = new();

    public ICommand SubmitCommand { get; }
    public ICommand DismissSuccessCommand { get; }
    public ICommand GoToProfileCommand { get; }
    public ICommand LoadBookingsCommand { get; }

    public BookingViewModel(IDataService dataService, SessionService sessionService)
    {
        _dataService = dataService;
        _sessionService = sessionService;

        SubmitCommand = new Command(async () => await SubmitAsync());
        DismissSuccessCommand = new Command(() => IsSuccess = false);
        GoToProfileCommand = new Command(async () => await Shell.Current.GoToAsync("//profile"));
        LoadBookingsCommand = new Command(async () => await LoadBookingsAsync());
    }

    private async Task SubmitAsync()
    {
        if (string.IsNullOrWhiteSpace(SelectedRoute))
        {
            await App.Current!.MainPage!.DisplayAlert("Помилка", "Оберіть маршрут", "OK");
            return;
        }

        IsBusy = true;
        try
        {
            var b = new BookingModel
            {
                Name = IsLoggedIn ? UserName : Name,
                Email = IsLoggedIn ? UserEmail : Email,
                Phone = Phone,
                RouteName = SelectedRoute,
                RouteKey = SelectedRoute,
                PeopleCount = PeopleCount,
                UserId = _sessionService.CurrentUser?.Id
            };
            await _dataService.CreateBookingAsync(b);
            IsSuccess = true;
            if (IsLoggedIn) await LoadBookingsAsync();
        }
        catch (Exception ex)
        {
            await App.Current!.MainPage!.DisplayAlert("Помилка", ex.Message, "OK");
        }
        finally
        {
            IsBusy = false;
        }
    }

    private async Task LoadBookingsAsync()
    {
        if (!IsLoggedIn || _sessionService.CurrentUser == null) return;
        var bs = await _dataService.GetUserBookingsAsync(_sessionService.CurrentUser.Id);
        MyBookings.Clear();
        foreach (var b in bs) MyBookings.Add(b);
    }
}