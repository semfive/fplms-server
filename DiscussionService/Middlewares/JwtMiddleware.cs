using Microsoft.AspNetCore.Http;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
// using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;
// using JwtDemoAPI.Helpers;

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
            Console.WriteLine(token);
            if (token != null)
            {
                await attachAccountToContext(context, token);
            }

            await _next(context);
        }

        private async Task attachAccountToContext(HttpContext context, string token)
        {
            try
            {
                using (var httpClient = new HttpClient())
                {
                    httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue(token);
                    var response = await httpClient.GetAsync(_configuration.GetConnectionString("AuthService"));
                    if (response.IsSuccessStatusCode)
                    {
                        var contents = await response.Content.ReadAsStringAsync();
                        var deserialized = JsonConvert.DeserializeObject(contents);
                        Console.WriteLine("GOT THE CONTENTS " + deserialized);
                        context.Items["User"] = deserialized;
                    }
                }
            }
            catch (Exception ex)
            {
                // if jwt validation fails then do nothing 
            }
        }

    }
}