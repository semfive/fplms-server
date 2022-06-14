using DiscussionService.Filters;
using DiscussionService.Contracts;
using DiscussionService.Data;
using DiscussionService.Repositories;
using JwtDemoAPI.Middleware;
using Microsoft.EntityFrameworkCore;
using Serilog;

try
{
    var builder = WebApplication.CreateBuilder(args);

    // Add services to the container.
    builder.Services.AddCors(options =>
        {
            options.AddDefaultPolicy(
                builder =>
                {
                    builder.WithOrigins("http://localhost:3000")
                           .AllowAnyHeader()
                           .AllowAnyMethod();
                });
        }
    );

    builder.Services.AddDbContext<RepositoryContext>(options => options.UseMySql(builder.Configuration.GetConnectionString("MySql"), new MySqlServerVersion(new Version())));
    builder.Services.AddScoped<IRepositoryWrapper, RepositoryWrapper>();
    builder.Services.AddScoped<ValidationFilterAttribute>();
    builder.Services.AddAutoMapper(AppDomain.CurrentDomain.GetAssemblies());

    // Serilog
    var logger = new LoggerConfiguration()
                        .ReadFrom.Configuration(builder.Configuration)
                        .Enrich.FromLogContext()
                        .CreateLogger();
    builder.Logging.ClearProviders();
    builder.Logging.AddSerilog(logger);

    builder.Services.AddControllers();
    // Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
    builder.Services.AddEndpointsApiExplorer();
    builder.Services.AddSwaggerGen();

    var app = builder.Build();
    // Configure the HTTP request pipeline.
    if (app.Environment.IsDevelopment())
    {
        app.UseSwagger();
        app.UseSwaggerUI();
    }

    app.UseHttpsRedirection();
    app.UseCors(options => options.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader());
    app.UseAuthorization();
    app.UseMiddleware<JwtMiddleware>();
    app.MapControllers();

    using (var scope = app.Services.CreateScope())
    {
        var services = scope.ServiceProvider;
        var context = services.GetRequiredService<RepositoryContext>();
        context.Database.Migrate();
    }

    app.Run();
}
catch (Exception ex)
{
    Log.Fatal(ex, "Host terminated unexpectedly");
}
finally
{
    Log.CloseAndFlush();
}
