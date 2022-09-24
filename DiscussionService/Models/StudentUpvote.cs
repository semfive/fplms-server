using System.ComponentModel.DataAnnotations.Schema;

namespace DiscussionService.Models
{
    public class StudentUpvote
    {
        public Guid StudentId { get; set; }
        public Student Student { get; set; }

        public Guid QuestionId { get; set; }
        public Question Question { get; set; }
    }
}