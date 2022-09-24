using DiscussionService.Models;
using Microsoft.EntityFrameworkCore;

namespace DiscussionService.Data
{
    public class RepositoryContext : DbContext
    {
        public RepositoryContext(DbContextOptions options) : base(options) { }
        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<StudentUpvote>()
               .HasKey(x => new { x.StudentId, x.QuestionId });
            modelBuilder.Entity<StudentUpvote>()
                .HasOne(pt => pt.Student)
                .WithMany(p => p.UpvotedQuestions)
                .HasForeignKey(pt => pt.StudentId);
            modelBuilder.Entity<StudentUpvote>()
                .HasOne(pt => pt.Question)
                .WithMany(t => t.Upvoters)
                .HasForeignKey(pt => pt.QuestionId);

            modelBuilder.Entity<StudentAnswerUpvote>()
                .HasKey(x => new { x.StudentId, x.AnswerId });
            modelBuilder.Entity<StudentAnswerUpvote>()
                .HasOne(pt => pt.Student)
                .WithMany(p => p.UpvotedAnswers)
                .HasForeignKey(pt => pt.StudentId);
            modelBuilder.Entity<StudentAnswerUpvote>()
                .HasOne(pt => pt.Answer)
                .WithMany(t => t.Upvoters)
                .HasForeignKey(pt => pt.AnswerId);
        }
        public DbSet<Student>? Students { get; set; }
        public DbSet<Lecturer>? Lecturers { get; set; }
        public DbSet<Question>? Questions { get; set; }
        public DbSet<Answer>? Answers { get; set; }
        public DbSet<Subject>? Subjects { get; set; }
        public DbSet<StudentUpvote>? StudentUpvotes { get; set; }
        public DbSet<StudentAnswerUpvote>? StudentAnswerUpvotes { get; set; }
    }
}