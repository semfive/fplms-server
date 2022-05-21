using Microsoft.AspNetCore.Mvc;
using AuthService.Dtos;
using AuthService.Jwt;
using System.Net.Http.Headers;

namespace AuthService.Controllers
{
    [ApiController]
    [Route("api/accounts")]

    public class AccountsController : ControllerBase
    {
        private readonly JwtHandler _jwtHandler;
        public AccountsController(JwtHandler jwtHandler)
        {
            _jwtHandler = jwtHandler;
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] AuthDto authDto)
        {
            var payload = await _jwtHandler.VerifyGoogleToken(authDto);

            if (payload == null)
            {
                return BadRequest("Invalid Authentication.");
            }

            var token = _jwtHandler.GenerateToken(payload);

            // TODO: send user information to Management and Discussion service

            return Ok(new AuthResponseDto
            {
                Token = token,
                IsAuthSuccessful = true
            });

        }

        [HttpPost("verify")]
        public IActionResult VerifyToken([FromHeader] string authorization)
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
    }
}