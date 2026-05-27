using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace Carpathians.DAL.Migrations
{
    /// <inheritdoc />
    public partial class FinalDatabaseSchema : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "LocationTag",
                table: "GalleryPhotos");

            migrationBuilder.RenameColumn(
                name: "Order",
                table: "RoutePoints",
                newName: "OrderIndex");

            migrationBuilder.RenameColumn(
                name: "UserPhone",
                table: "Bookings",
                newName: "GuestPhone");

            migrationBuilder.RenameColumn(
                name: "UserName",
                table: "Bookings",
                newName: "GuestName");

            migrationBuilder.RenameColumn(
                name: "UserEmail",
                table: "Bookings",
                newName: "GuestEmail");

            migrationBuilder.AddColumn<string>(
                name: "MapUrl",
                table: "Routes",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<string>(
                name: "Slug",
                table: "Routes",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<string>(
                name: "WhatToTake",
                table: "Routes",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<string>(
                name: "Description",
                table: "RoutePoints",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<int>(
                name: "RouteId",
                table: "GalleryPhotos",
                type: "integer",
                nullable: true);

            migrationBuilder.AddColumn<int>(
                name: "UserId",
                table: "Bookings",
                type: "integer",
                nullable: true);

            migrationBuilder.CreateTable(
                name: "RoutePointPhotos",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    RoutePointId = table.Column<int>(type: "integer", nullable: false),
                    Url = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_RoutePointPhotos", x => x.Id);
                    table.ForeignKey(
                        name: "FK_RoutePointPhotos_RoutePoints_RoutePointId",
                        column: x => x.RoutePointId,
                        principalTable: "RoutePoints",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Users",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Name = table.Column<string>(type: "text", nullable: false),
                    Email = table.Column<string>(type: "text", nullable: false),
                    PasswordHash = table.Column<string>(type: "text", nullable: false),
                    AvatarUrl = table.Column<string>(type: "text", nullable: true),
                    CreatedAt = table.Column<DateTime>(type: "timestamp with time zone", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Users", x => x.Id);
                });

            migrationBuilder.CreateIndex(
                name: "IX_GalleryPhotos_RouteId",
                table: "GalleryPhotos",
                column: "RouteId");

            migrationBuilder.CreateIndex(
                name: "IX_Bookings_UserId",
                table: "Bookings",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_RoutePointPhotos_RoutePointId",
                table: "RoutePointPhotos",
                column: "RoutePointId");

            migrationBuilder.AddForeignKey(
                name: "FK_Bookings_Users_UserId",
                table: "Bookings",
                column: "UserId",
                principalTable: "Users",
                principalColumn: "Id",
                onDelete: ReferentialAction.SetNull);

            migrationBuilder.AddForeignKey(
                name: "FK_GalleryPhotos_Routes_RouteId",
                table: "GalleryPhotos",
                column: "RouteId",
                principalTable: "Routes",
                principalColumn: "Id",
                onDelete: ReferentialAction.SetNull);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Bookings_Users_UserId",
                table: "Bookings");

            migrationBuilder.DropForeignKey(
                name: "FK_GalleryPhotos_Routes_RouteId",
                table: "GalleryPhotos");

            migrationBuilder.DropTable(
                name: "RoutePointPhotos");

            migrationBuilder.DropTable(
                name: "Users");

            migrationBuilder.DropIndex(
                name: "IX_GalleryPhotos_RouteId",
                table: "GalleryPhotos");

            migrationBuilder.DropIndex(
                name: "IX_Bookings_UserId",
                table: "Bookings");

            migrationBuilder.DropColumn(
                name: "MapUrl",
                table: "Routes");

            migrationBuilder.DropColumn(
                name: "Slug",
                table: "Routes");

            migrationBuilder.DropColumn(
                name: "WhatToTake",
                table: "Routes");

            migrationBuilder.DropColumn(
                name: "Description",
                table: "RoutePoints");

            migrationBuilder.DropColumn(
                name: "RouteId",
                table: "GalleryPhotos");

            migrationBuilder.DropColumn(
                name: "UserId",
                table: "Bookings");

            migrationBuilder.RenameColumn(
                name: "OrderIndex",
                table: "RoutePoints",
                newName: "Order");

            migrationBuilder.RenameColumn(
                name: "GuestPhone",
                table: "Bookings",
                newName: "UserPhone");

            migrationBuilder.RenameColumn(
                name: "GuestName",
                table: "Bookings",
                newName: "UserName");

            migrationBuilder.RenameColumn(
                name: "GuestEmail",
                table: "Bookings",
                newName: "UserEmail");

            migrationBuilder.AddColumn<string>(
                name: "LocationTag",
                table: "GalleryPhotos",
                type: "text",
                nullable: false,
                defaultValue: "");
        }
    }
}
