using System.Collections.ObjectModel;
using System.Threading.Tasks;
using System.Windows.Input;
using Carpathians.MAUI.Models;
using Carpathians.MAUI.Services;

namespace Carpathians.MAUI.ViewModels;

public class ProfileViewModel : BaseViewModel
{
    private readonly IDataService _dataService;
    private readonly SessionService _sessionService;

    public bool IsLoggedIn => _sessionService.IsLoggedIn;

    private bool _isLoginTab = true;
    public bool IsLoginTab { get => _isLoginTab; set { _isLoginTab = value; OnPropertyChanged(); } }

    private string _loginEmail = string.Empty;
    public string LoginEmail { get => _loginEmail; set { _loginEmail = value; OnPropertyChanged(); } }

    private string _loginPassword = string.Empty;
    public string LoginPassword { get => _loginPassword; set { _loginPassword = value; OnPropertyChanged(); } }

    private string _regName = string.Empty;
    public string RegName { get => _regName; set { _regName = value; OnPropertyChanged(); } }

    private string _regEmail = string.Empty;
    public string RegEmail { get => _regEmail; set { _regEmail = value; OnPropertyChanged(); } }

    private string _regPassword = string.Empty;
    public string RegPassword { get => _regPassword; set { _regPassword = value; OnPropertyChanged(); } }

    private string _regConfirmPassword = string.Empty;
    public string RegConfirmPassword { get => _regConfirmPassword; set { _regConfirmPassword = value; OnPropertyChanged(); } }

    private string _userPhone = string.Empty;
    public string UserPhone { get => _userPhone; set { _userPhone = value; OnPropertyChanged(); } }

    private bool _showPassword;
    public bool ShowPassword { get => _showPassword; set { _showPassword = value; OnPropertyChanged(); } }

    public string? UserName => _sessionService.CurrentUser?.Name;
    public string? UserEmail => _sessionService.CurrentUser?.Email;
    public string UserInitials => string.IsNullOrEmpty(UserName) ? "?" : UserName.Substring(0, 1).ToUpper();

    public ObservableCollection<BookingModel> MyBookings { get; } = new();

    public ICommand LoginCommand { get; }
    public ICommand RegisterCommand { get; }
    public ICommand LogoutCommand { get; }
    public ICommand ToggleTabCommand { get; }
    public ICommand LoadProfileCommand { get; }
    public ICommand TogglePasswordCommand { get; }
    public ICommand SaveProfileCommand { get; }

    public ProfileViewModel(IDataService dataService, SessionService sessionService)
    {
        _dataService = dataService;
        _sessionService = sessionService;

        LoginCommand = new Command(async () => await LoginAsync());
        RegisterCommand = new Command(async () => await RegisterAsync());
        LogoutCommand = new Command(async () => await LogoutAsync());
        ToggleTabCommand = new Command(() => IsLoginTab = !IsLoginTab);
        LoadProfileCommand = new Command(async () => await LoadProfileAsync());
        TogglePasswordCommand = new Command(() => ShowPassword = !ShowPassword);
        SaveProfileCommand = new Command(async () => await SaveProfileAsync());
    }

    private async Task LoginAsync()
    {
        if (string.IsNullOrWhiteSpace(LoginEmail) || string.IsNullOrWhiteSpace(LoginPassword))
        {
            await App.Current!.MainPage!.DisplayAlert("Помилка", "Заповніть всі поля", "OK");
            return;
        }
        try
        {
            var user = await _dataService.LoginAsync(LoginEmail, LoginPassword);
            if (user != null)
            {
                _sessionService.Login(user);
                RefreshState();
                await LoadProfileAsync();
            }
            else
            {
                await App.Current!.MainPage!.DisplayAlert("Помилка", "Невірний email або пароль", "OK");
            }
        }
        catch
        {
            await App.Current!.MainPage!.DisplayAlert("Помилка", "Не вдалося увійти", "OK");
        }
    }

    private async Task RegisterAsync()
    {
        if (string.IsNullOrWhiteSpace(RegName) || string.IsNullOrWhiteSpace(RegEmail) || string.IsNullOrWhiteSpace(RegPassword))
        {
            await App.Current!.MainPage!.DisplayAlert("Помилка", "Заповніть всі поля", "OK");
            return;
        }
        if (RegPassword != RegConfirmPassword)
        {
            await App.Current!.MainPage!.DisplayAlert("Помилка", "Паролі не збігаються", "OK");
            return;
        }
        try
        {
            var user = await _dataService.RegisterAsync(RegName, RegEmail, RegPassword);
            _sessionService.Login(user);
            RefreshState();
            await LoadProfileAsync();
            await App.Current!.MainPage!.DisplayAlert("Успіх", "Реєстрацію завершено!", "OK");
        }
        catch (Exception ex)
        {
            await App.Current!.MainPage!.DisplayAlert("Помилка", ex.Message, "OK");
        }
    }

    private async Task LogoutAsync()
    {
        var confirm = await App.Current!.MainPage!.DisplayAlert("Вихід", "Ви дійсно хочете вийти?", "Так", "Скасувати");
        if (confirm)
        {
            _sessionService.Logout();
            MyBookings.Clear();
            RefreshState();
        }
    }

    private async Task LoadProfileAsync()
    {
        if (!IsLoggedIn || _sessionService.CurrentUser == null) return;
        try
        {
            var bs = await _dataService.GetUserBookingsAsync(_sessionService.CurrentUser.Id);
            MyBookings.Clear();
            foreach (var b in bs) MyBookings.Add(b);
        }
        catch { }
    }

    private void RefreshState()
    {
        OnPropertyChanged(nameof(IsLoggedIn));
        OnPropertyChanged(nameof(UserName));
        OnPropertyChanged(nameof(UserEmail));
        OnPropertyChanged(nameof(UserInitials));
    }

    private async Task SaveProfileAsync()
    {
        if (!IsLoggedIn || _sessionService.CurrentUser == null) return;
        try
        {
            var user = _sessionService.CurrentUser;
            if (!string.IsNullOrWhiteSpace(UserName)) user.Name = UserName;
            if (!string.IsNullOrWhiteSpace(UserEmail)) user.Email = UserEmail;
            await _dataService.UpdateUserAsync(user);
            RefreshState();
            await App.Current!.MainPage!.DisplayAlert("Успіх", "Профіль оновлено", "OK");
        }
        catch
        {
            await App.Current!.MainPage!.DisplayAlert("Помилка", "Не вдалося зберегти профіль", "OK");
        }
    }
}