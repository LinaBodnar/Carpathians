using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace Carpathians.MAUI.Services
{
    public class ApiService
    {
        private readonly HttpClient _httpClient;

        // Якщо тестуєш на симуляторі Android, адреса комп'ютера зазвичай 10.0.2.2. 
        // Якщо запускаєш як Windows-додаток — став http://localhost:7000 (або свій порт з бекенду)
        private readonly string _baseUrl = DeviceInfo.Platform == DevicePlatform.Android
            ? "http://10.0.2.2:7000/api/"
            : "http://localhost:7000/api/";

        public ApiService()
        {
            _httpClient = new HttpClient();
        }

        // Універсальний метод для отримання даних (GET)
        public async Task<T?> GetAsync<T>(string endpoint)
        {
            try
            {
                var response = await _httpClient.GetAsync($"{_baseUrl}{endpoint}");
                if (response.IsSuccessStatusCode)
                {
                    var json = await response.Content.ReadAsStringAsync();
                    return JsonConvert.DeserializeObject<T>(json);
                }
                return default;
            }
            catch (Exception)
            {
                // Тут можна додати логування помилок мережі
                return default;
            }
        }
    }
}