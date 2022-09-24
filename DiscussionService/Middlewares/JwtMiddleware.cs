using DiscussionService.Dtos;
using Microsoft.AspNetCore.Http;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;

namespace JwtDemoAPI.Middleware
{
    public class JwtMiddleware
    {
        private readonly RequestDelegate _next;
        private readonly IConfiguration _configuration;

        public JwtMiddleware(IConfiguration configuration, RequestDelegate next)
        {
            _configuration = configuration;
            _next = next;
        }

        public async Task Invoke(HttpContext context)
        {
            var token = context.Request.Headers["Authorization"].FirstOrDefault()?.Split(" ").Last();
            if (token != null)
            {
                await AttachAccountToContext(context, token);
            }

            await _next(context);
        }

        private async Task AttachAccountToContext(HttpContext context, string token)
        {
            try
            {
                using var httpClient = new HttpClient();
                ////{
                //httpClient.DefaultRequestHeaders.Clear();
                //httpClient.DefaultRequestHeaders.Accept.Add(new System.Net.Http.Headers.MediaTypeWithQualityHeaderValue("application/json"));
                //httpClient.DefaultRequestHeaders.TryAddWithoutValidation("Content-Type", "application/json");
                //httpClient.DefaultRequestHeaders.TryAddWithoutValidation("Authorization", token);

                httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue(token);
                ////httpClient.DefaultRequestHeaders.Add("Accept", "text/html,application/xhtml+xml+json");
                ////httpClient.DefaultRequestHeaders.Add("Authorization", token);
                ///

                //var json = JsonConvert.SerializeObject({ Name = "ss" });
                //var data = new StringContent("", Encoding.UTF8, "application/json");
                var response = await httpClient.GetAsync(_configuration.GetConnectionString("AuthService"));
                response.EnsureSuccessStatusCode(); // throws if not 200-299
                var responseString = await response.Content.ReadFromJsonAsync<GetVerifiedUserDto>();
                //Console.WriteLine(responseString.Email);
                context.Items["UserEmail"] = responseString.Email;
                context.Items["UserRole"] = responseString.Role;
                //if (response.Content is object && response.Content.Headers.ContentType.MediaType == "application/json")
                //{
                //    var contentStream = await response.Content.ReadAsStreamAsync();

                //    using var streamReader = new StreamReader(contentStream);
                //    using var jsonReader = new JsonTextReader(streamReader);

                //    JsonSerializer serializer = new JsonSerializer();

                //    try
                //    {
                //        var serialized = serializer.Deserialize<GetVerifiedUserDto>(jsonReader);
                //    }
                //    catch (JsonReaderException)
                //    {
                //        Console.WriteLine("Invalid JSON.");
                //    }
                //}
                //else
                //{
                //    Console.WriteLine("HTTP Response was invalid and cannot be deserialised.");
                //}
                //Console.WriteLine(response);      
                //response.Wait();
                //if (response.IsSuccessStatusCode)
                //{
                //var contents = response.Content.ReadFromJsonAsync<GetVerifiedUserDto>();
                // var contents = await response.Content.ReadAsStringAsync();
                // var deserialized = JsonConvert.DeserializeObject(contents);
                // string email = deserialized.GetType().GetProperty("email").GetValue(deserialized, null) as string;
                // string role = deserialized.GetType().GetProperty("role").GetValue(deserialized, null) as string;
                // context.Items["email"] = email;
                // context.Items["role"] = role;
                // if (deserialized)
                //context.Items["userEmail"] = contents.email;
                //context.Items["userRole"] = contents.role;
                //Console.WriteLine(contents.email);
                //}
                //}
            }
            catch (Exception ex)
            {
                // if jwt validation fails then do nothing 
            }
        }

    }
}