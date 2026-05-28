using Carpathians.MAUI.ViewModels;
namespace Carpathians.MAUI.Views;
public partial class GalleryPage : ContentPage
{
    public GalleryPage(GalleryViewModel vm)
    {
        InitializeComponent();
        BindingContext = vm;
    }
    protected override void OnAppearing()
    {
        base.OnAppearing();
        if (BindingContext is GalleryViewModel vm && vm.LoadPhotosCommand.CanExecute(null))
        {
            vm.LoadPhotosCommand.Execute(null);
        }
    }
}