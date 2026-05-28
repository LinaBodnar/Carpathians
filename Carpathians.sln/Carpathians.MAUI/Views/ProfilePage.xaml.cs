using Carpathians.MAUI.ViewModels;
namespace Carpathians.MAUI.Views;
public partial class ProfilePage : ContentPage
{
    public ProfilePage(ProfileViewModel vm)
    {
        InitializeComponent();
        BindingContext = vm;
    }
    protected override void OnAppearing()
    {
        base.OnAppearing();
        if (BindingContext is ProfileViewModel vm && vm.LoadProfileCommand.CanExecute(null))
        {
            vm.LoadProfileCommand.Execute(null);
        }
    }
}