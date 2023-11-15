# Learn about building .NET container images:
# https://github.com/dotnet/dotnet-docker/blob/main/samples/README.md
FROM mcr.microsoft.com/dotnet/sdk:7.0 AS build

# install System.Drawing native dependencies
RUN apt-get update
RUN apt-get install -y --allow-unauthenticated 
RUN apt-get install -y libc6-dev 
RUN apt-get install -y libgdiplus
RUN apt-get install -y libx11-dev 

WORKDIR /source

# copy csproj and restore as distinct layers
COPY WebPipi/*.csproj .
RUN dotnet restore --use-current-runtime  

# copy everything else and build app
COPY WebPipi/. .
RUN dotnet publish -o /app


# final stage/image
FROM mcr.microsoft.com/dotnet/aspnet:7.0
WORKDIR /app
COPY --from=build /app .
ENTRYPOINT ["dotnet", "WebPipi.dll"]
