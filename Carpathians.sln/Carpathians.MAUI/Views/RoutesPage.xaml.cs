using Carpathians.MAUI.ViewModels;
namespace Carpathians.MAUI.Views;
public partial class RoutesPage : ContentPage
{
    public RoutesPage(RoutesViewModel vm)
    {
        InitializeComponent();
        BindingContext = vm;
    }
    protected override void OnAppearing()
    {
        base.OnAppearing();
        if (BindingContext is RoutesViewModel vm && vm.LoadRoutesCommand.CanExecute(null))
        {
            vm.LoadRoutesCommand.Execute(null);
        }
    }
}