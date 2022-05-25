FROM mcr.microsoft.com/dotnet/aspnet:6.0-focal AS base
WORKDIR /app
EXPOSE 7218

ENV ASPNETCORE_URLS=http://+:7218

FROM mcr.microsoft.com/dotnet/sdk:6.0-focal AS build
WORKDIR /src
COPY ["DiscussionService.csproj", "./"]
RUN dotnet restore "DiscussionService.csproj"
COPY . .
WORKDIR "/src/."
RUN dotnet build "DiscussionService.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "DiscussionService.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "DiscussionService.dll"]