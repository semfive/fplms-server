using System.ComponentModel.DataAnnotations.Schema;

namespace DiscussionService.Models
{
    public class StudentAnswerUpvote
    {
        public Guid StudentId { get; set; }
        public Student Student { get; set; }

        public Guid AnswerId { get; set; }
        public Answer Answer { get; set; }
    }
}