using Microsoft.AspNetCore.Mvc;
using AuthService.Dtos;
using AuthService.Jwt;
using System.Net.Http.Headers;
using Newtonsoft.Json;
using System.Text;
using AuthService.ActionFilters;
using System.Text.RegularExpressions;

namespace AuthService.Controllers
{
    [ApiController]
    [Route("api/auth/accounts")]
    public class AccountsController : ControllerBase
    {
        private readonly JwtHandler _jwtHandler;
        private IConfiguration _config;
        public AccountsController(JwtHandler jwtHandler, IConfiguration config)
        {
            _jwtHandler = jwtHandler;
            _config = config;
        }

        [HttpPost("login")]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> Login([FromBody] AuthDto authDto)
        {
            try
            {
                var payload = await _jwtHandler.VerifyGoogleToken(authDto);

                if (payload == null)
                {
                    return BadRequest("Invalid Authentication.");
                }

                if (!payload.Email.Contains("@fpt") && !payload.Email.Contains("@fe"))
                {
                    return BadRequest("Invalid Authentication.");
                }

                var token = _jwtHandler.GenerateToken(payload);

                // TODO: send user information to Management and Discussion service
                using (var httpClient = new HttpClient())
                {
                    var userDto = new CreateUserDto
                    {
                        Name = payload.Name,
                        Email = payload.Email,
                        Picture = payload.Picture
                    };
                    var json = JsonConvert.SerializeObject(userDto);
                    var data = new StringContent(json, Encoding.UTF8, "application/json");

                    var managementUserDto = new ManagementCreateUserDto
                    {
                        name = payload.Name,
                        email = payload.Email,
                        imageUrl = payload.Picture,
                        code = Regex.Match(payload.Email, @"\d+").Value
                    };

                    if (userDto.Email.Contains("@fpt.edu.vn"))
                    {
                        Console.WriteLine("\nSend POST request to DiscussionService");
                        var result = httpClient.PostAsync(_config.GetConnectionString("DiscussionService") + "students", data);
                        result.Wait();

                        Console.WriteLine("\nSend POST request to ManagementService: " + managementUserDto);
                        managementUserDto.isLecturer = false;
                        var managementJson = JsonConvert.SerializeObject(managementUserDto);
                        var managementData = new StringContent(managementJson, Encoding.UTF8, "application/json");
                        var managementResult = httpClient.PostAsync(_config.GetConnectionString("ManagementService"), managementData);
                        managementResult.Wait();
                    }

                    if (userDto.Email.Contains("@fe.edu.vn"))
                    {
                        Console.WriteLine("\nSend POST request to DiscussionService");
                        var result = httpClient.PostAsync(_config.GetConnectionString("DiscussionService") + "lecturers", data);
                        result.Wait();

                        Console.WriteLine("\nSend POST request to ManagementService: " + managementUserDto);
                        managementUserDto.isLecturer = true;
                        var managementJson = JsonConvert.SerializeObject(managementUserDto);
                        var managementData = new StringContent(managementJson, Encoding.UTF8, "application/json");
                        var managementResult = httpClient.PostAsync(_config.GetConnectionString("ManagementService"), managementData);
                        managementResult.Wait();
                    }
                }

                return Ok(new AuthResponseDto
                {
                    Token = token,
                    IsAuthSuccessful = true,
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpGet("verify")]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public IActionResult VerifyToken([FromHeader] string authorization)
        {
            try
            {
                if (AuthenticationHeaderValue.TryParse(authorization, out var headerValue))
                {
                    var payload = _jwtHandler.ValidateToken(Convert.ToString(headerValue));

                    if (payload == null)
                    {
                        return BadRequest("Invalid Authentication.");
                    }
                    return Ok(payload);
                }
                return BadRequest();
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpOptions("login")]
        public IActionResult Preflight()
        {
            return Ok();
        }
    }
}