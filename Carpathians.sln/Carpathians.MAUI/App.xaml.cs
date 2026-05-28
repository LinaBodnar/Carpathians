using Carpathians.MAUI.Views;

namespace Carpathians.MAUI;

public partial class App : Application
{
    public App()
    {
        InitializeComponent();

        MainPage = new AppShell();
    }
}