using Microsoft.AspNetCore.Mvc;
using AuthService.Dtos;
using AuthService.Jwt;
using System.Net.Http.Headers;
using Newtonsoft.Json;
using System.Text;
using AuthService.ActionFilters;

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
                using var httpClient = new HttpClient();
                var userDto = new UserDto
                {
                    Email = payload.Email,
                    Role = payload.Email.Contains("@fpt.edu.vn") ? "Student" : "Lecturer"
                };
                var json = JsonConvert.SerializeObject(userDto);
                var data = new StringContent(json, Encoding.UTF8, "application/json");
                var result = await httpClient.PostAsync(_config.GetSection("").Value, data);

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

        [HttpPost("verify")]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public IActionResult VerifyToken([FromHeader] string authorization)
        {
            try
            {
                if (AuthenticationHeaderValue.TryParse(authorization, out var headerValue))
                {
                    var payload = _jwtHandler.ValidateToken(headerValue.Parameter);

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
    }
}