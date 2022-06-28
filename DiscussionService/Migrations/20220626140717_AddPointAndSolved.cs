using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace DiscussionService.Migrations
{
    public partial class AddPointAndSolved : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "Point",
                table: "Students",
                type: "int",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<bool>(
                name: "Solved",
                table: "Questions",
                type: "tinyint(1)",
                nullable: false,
                defaultValue: false);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Point",
                table: "Students");

            migrationBuilder.DropColumn(
                name: "Solved",
                table: "Questions");
        }
    }
}
