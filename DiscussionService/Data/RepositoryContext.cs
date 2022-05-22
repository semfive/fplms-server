using DiscussionService.Models;
using Microsoft.EntityFrameworkCore;

namespace DiscussionService.Data
{
    public class RepositoryContext : DbContext
    {
        public RepositoryContext(DbContextOptions options) : base(options) { }

        public DbSet<Student>? Students { get; set; }
        public DbSet<Lecturer>? Lecturers { get; set; }
    }
}