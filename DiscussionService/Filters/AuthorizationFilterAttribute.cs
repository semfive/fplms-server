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
        var UserId = context.HttpContext.Items["User"];
        if (UserId == null)
        {
            // not logged in
            context.Result = new JsonResult(new { message = "Unauthorized" }) { StatusCode = StatusCodes.Status401Unauthorized };
        }
    }
}