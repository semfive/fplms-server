using Microsoft.AspNetCore.Mvc;
using AuthService.Dtos;
using AuthService.Jwt;
using System.Net.Http.Headers;
using Newtonsoft.Json;
using System.Text;

namespace AuthService.Controllers
{
    [ApiController]
    [Route("api/accounts")]

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
        public async Task<IActionResult> Login([FromBody] AuthDto authDto)
        {
            try
            {
                var payload = await _jwtHandler.VerifyGoogleToken(authDto);

                if (payload == null)
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
                    IsAuthSuccessful = true
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }

        }

        [HttpPost("verify")]
        public IActionResult VerifyToken([FromHeader] string authorization)
        {
            try
            {
                if (AuthenticationHeaderValue.TryParse(authorization, out var headerValue))
                {
                    // AuthDto authDto = new AuthDto
                    // {
                    //     IdToken = headerValue.Parameter,
                    //     Provider = "GOOGLE"
                    // };

                    //var payload = await _jwtHandler.VerifyGoogleToken(authDto);

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