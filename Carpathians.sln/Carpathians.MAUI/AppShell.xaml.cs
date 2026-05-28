using Carpathians.MAUI.Views;

namespace Carpathians.MAUI;

public partial class AppShell : Shell
{
    public AppShell()
    {
        InitializeComponent();
        
        Routing.RegisterRoute("routedetail", typeof(RouteDetailPage));
        Routing.RegisterRoute("mapview", typeof(MapViewPage));
    }
}