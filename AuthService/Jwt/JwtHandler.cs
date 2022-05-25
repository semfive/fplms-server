using AuthService.Dtos;
using Google.Apis.Auth;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace AuthService.Jwt
{
    public class JwtHandler
    {
        private readonly IConfigurationSection _googleAuthSettings;
        private readonly IConfigurationSection _jwtSettings;
        private readonly IConfiguration _configuration;

        public JwtHandler(IConfiguration configuration)
        {
            _configuration = configuration;
            _googleAuthSettings = _configuration.GetSection("GoogleAuthSettings");
            _jwtSettings = _configuration.GetSection("JwtSettings");
        }

        private SigningCredentials GetSigningCredentials()
        {
            var key = Encoding.UTF8.GetBytes(_jwtSettings.GetSection("securityKey").Value);
            var secret = new SymmetricSecurityKey(key);

            return new SigningCredentials(secret, SecurityAlgorithms.HmacSha256);
        }

        private List<Claim> GetClaims(GoogleJsonWebSignature.Payload payload)

        {
            var claims = new List<Claim>
            {
                new Claim(ClaimTypes.Email, payload.Email)
            };

            if (payload.Email.Contains("@fpt"))
            {
                claims.Add(new Claim(ClaimTypes.Role, "Student"));
            }

            if (payload.Email.Contains("@fe"))
            {
                claims.Add(new Claim(ClaimTypes.Role, "Lecturer"));
            }

            return claims;
        }

        private JwtSecurityToken GenerateTokenOptions(SigningCredentials signingCredentials, List<Claim> claims)
        {
            var tokenOptions = new JwtSecurityToken(
                issuer: _jwtSettings.GetSection("validIssuer").Value,
                audience: _jwtSettings.GetSection("validAudience").Value,
                claims: claims,
                expires: DateTime.Now.AddMinutes(Convert.ToDouble(_jwtSettings.GetSection("expiryInMinutes").Value)),
                signingCredentials: signingCredentials
            );

            return tokenOptions;
        }

        public string GenerateToken(GoogleJsonWebSignature.Payload payload)
        {
            var signingCredentials = GetSigningCredentials();
            var claims = GetClaims(payload);

            foreach (var claim in claims)
            {
                Console.WriteLine($"{claim.Type}: {claim.Value}");
            }

            var tokenOptions = GenerateTokenOptions(signingCredentials, claims);
            var handler = new JwtSecurityTokenHandler();
            var token = handler.WriteToken(tokenOptions);

            return token;
        }

        public UserDto ValidateToken(string token)
        {
            var key = Encoding.UTF8.GetBytes(_jwtSettings.GetSection("securityKey").Value);
            var secret = new SymmetricSecurityKey(key);

            var tokenHandler = new JwtSecurityTokenHandler();
            try
            {
                tokenHandler.ValidateToken(token, new TokenValidationParameters
                {
                    ValidateIssuerSigningKey = true,
                    IssuerSigningKey = secret,
                    ValidateIssuer = true,
                    ValidateAudience = true,
                    ClockSkew = TimeSpan.Zero
                }, out SecurityToken validatedToken);

                var jwtToken = (JwtSecurityToken)validatedToken;

                var userEmail = jwtToken.Claims.First(x => x.Type == "Email").Value;
                var userRole = jwtToken.Claims.First(x => x.Type == "Role").Value;

                return new UserDto
                {
                    Email = userEmail,
                    Role = userRole
                };
            }
            catch (Exception ex)
            {
                return null;
            }
        }

        public async Task<GoogleJsonWebSignature.Payload> VerifyGoogleToken(AuthDto authDto)
        {
            try
            {
                var settings = new GoogleJsonWebSignature.ValidationSettings()
                {
                    Audience = new List<string>()
                    {
                        _googleAuthSettings.GetSection("clientId").Value
                    }
                };

                var payload = await GoogleJsonWebSignature.ValidateAsync(authDto.IdToken, settings);
                return payload;
            }
            catch (Exception ex)
            {
                return null;
            }
        }
    }
}