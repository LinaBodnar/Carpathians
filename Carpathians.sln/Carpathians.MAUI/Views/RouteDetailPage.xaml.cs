using Carpathians.MAUI.ViewModels;
namespace Carpathians.MAUI.Views;
public partial class RouteDetailPage : ContentPage
{
    public RouteDetailPage(RouteDetailViewModel vm)
    {
        InitializeComponent();
        BindingContext = vm;
    }
}