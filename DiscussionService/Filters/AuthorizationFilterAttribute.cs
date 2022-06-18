// using System.Net.Http.Headers;
// using System.Text;
// using Microsoft.AspNetCore.Authorization;
// using Microsoft.AspNetCore.Mvc;
// using Microsoft.AspNetCore.Mvc.Filters;
// using Newtonsoft.Json;

// public class AuthorizationFilterAttribute : AuthorizeAttribute, IAuthorizationFilter
// {
//     private readonly IConfiguration _config;
//     public AuthorizationFilterAttribute(IConfiguration config)
//     {
//         _config = config;
//     }

//     public async void OnAuthorization(AuthorizationFilterContext context)
//     {
//         var token = Convert.ToString(context.HttpContext.Request.Headers.Authorization);

//         using (var httpClient = new HttpClient())
//         {
//             httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue(token);
//             var response = await httpClient.GetAsync(_config.GetConnectionString("AuthService"));
//             // response.Wait();
//             if (!response.IsSuccessStatusCode)
//             {
//                 context.Result = new RedirectResult("http://localhost:3000/login");
//             }
//             var contents = await response.Content.ReadAsStringAsync();
//             context.HttpContext.Request.Headers.Add("X-User", contents);
//         }
//     }
// }

using System.Net.Http.Headers;
using System.Text;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using Newtonsoft.Json;

[AttributeUsage(AttributeTargets.Class | AttributeTargets.Method)]
public class AuthorizationFilterAttribute : Attribute, IAuthorizationFilter
{
    public void OnActionExecuted(ActionExecutedContext context)
    {
        throw new NotImplementedException();
    }

    // public async void OnActionExecuting(ActionExecutingContext context)
    // {
    //     var token = Convert.ToString(context.HttpContext.Request.Headers.Authorization);
    //     using (var httpClient = new HttpClient())
    //     {
    //         httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue(token);
    //         var response = await httpClient.GetAsync("http://fplms-authservice-clusterip:7209/api/auth/accounts/verify/");
    //         if (!response.IsSuccessStatusCode)
    //         {
    //             context.Result = new RedirectResult("http://localhost:3000/login");
    //         }
    //         var contents = await response.Content.ReadAsStringAsync();
    //         context.HttpContext.Request.Headers.Add("X-User", contents);
    //     }
    // }

    public void OnAuthorization(AuthorizationFilterContext context)
    {
        //Console.WriteLine(context.HttpContext.Items["UserEmail"]);
        //using var httpClient = new HttpClient();
        ////{
        //httpClient.DefaultRequestHeaders.Clear();
        //httpClient.DefaultRequestHeaders.Accept.Add(new System.Net.Http.Headers.MediaTypeWithQualityHeaderValue("application/json"));
        //httpClient.DefaultRequestHeaders.TryAddWithoutValidation("Content-Type", "application/json");
        //httpClient.DefaultRequestHeaders.TryAddWithoutValidation("Authorization", (string?)context.HttpContext.Items["token"]);

        //httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue(token);
        //httpClient.DefaultRequestHeaders.Add("Accept", "text/html,application/xhtml+xml+json");
        //httpClient.DefaultRequestHeaders.Add("Authorization", token);
        //var response = httpClient.GetAsync("https://silent-hairs-cover-14-169-34-149.loca.lt/api/auth/accounts/verify").Result;
        //response.EnsureSuccessStatusCode(); // throws if not 200-299
        //var userEmail = context.HttpContext.Items["userEmail"];
        //var userRole = context.HttpContext.Items["userRole"];
        //Console.WriteLine(context.HttpContext.Items["userEmail"]);

        // var userRole = user.GetType().GetProperty("role").GetValue(user, null) as string;
        // string userRole = context.HttpContext.Items["user"].GetType().GetProperty("role").GetValue(context.HttpContext.Items["user"], null) as string;
        var userEmail = context.HttpContext.Items["UserEmail"];
        var userRole = context.HttpContext.Items["UserRole"];
        if (userEmail == null || userRole == null)
        {
            // not logged in
            context.Result = new JsonResult(new { message = "Unauthorized" }) { StatusCode = StatusCodes.Status401Unauthorized };
            return;
        }
    }
}