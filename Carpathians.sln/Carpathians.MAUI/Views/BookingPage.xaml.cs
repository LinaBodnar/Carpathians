using Carpathians.MAUI.ViewModels;
namespace Carpathians.MAUI.Views;
public partial class BookingPage : ContentPage
{
    public BookingPage(BookingViewModel vm)
    {
        InitializeComponent();
        BindingContext = vm;
    }
}