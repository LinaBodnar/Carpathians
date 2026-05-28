using System;
using System.Globalization;
using Microsoft.Maui.Controls;
using Microsoft.Maui.Graphics;

namespace Carpathians.MAUI.Converters
{
    public class BoolToAccentColorConverter : IValueConverter
    {
        public Color TrueColor { get; set; } = Color.FromArgb("#4CAF50");
        public Color FalseColor { get; set; } = Color.FromArgb("#bbbbbb");
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (value is bool b) return b ? TrueColor : FalseColor;
            return FalseColor;
        }
        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture) => throw new NotImplementedException();
    }

    public class InvertedBoolToAccentColorConverter : IValueConverter
    {
        public Color TrueColor { get; set; } = Color.FromArgb("#4CAF50");
        public Color FalseColor { get; set; } = Color.FromArgb("#bbbbbb");
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (value is bool b) return b ? FalseColor : TrueColor;
            return TrueColor;
        }
        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture) => throw new NotImplementedException();
    }
}
