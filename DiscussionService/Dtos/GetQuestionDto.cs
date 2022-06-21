using DiscussionService.Models;

namespace DiscussionService.Dtos
{
    public class GetQuestionDto
    {
        public Guid Id { get; set; }
        public string? Title { get; set; }
        public string? Content { get; set; }
        public DateTimeOffset CreatedDate { get; set; }
        public DateTimeOffset ModifiedDate { get; set; }
        public bool Removed { get; set; }
        public string? RemovedBy { get; set; }
        // public Guid StudentId { get; set; }
        public GetStudentDto Student { get; set; }
        public GetSubjectDto Subject { get; set; }
        public List<GetAnswerDto>? Answers { get; set; }
    }
}